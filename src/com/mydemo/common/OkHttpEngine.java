package com.mydemo.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class OkHttpEngine {

	private static OkHttpEngine okHttpEngine;

	private OkHttpClient mOkHttpClient;

	public static synchronized void initEngine() {
		okHttpEngine = new OkHttpEngine();
	}

	public static OkHttpEngine getInstance() {
		if (okHttpEngine == null) {
			initEngine();
		}
		return okHttpEngine;
	}

	private OkHttpEngine() {
		mOkHttpClient = new OkHttpClient();
		mOkHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
		mOkHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
		mOkHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
	}

	public OkHttpEngine setCache(Context mContext) {
		File sdcache = mContext.getExternalCacheDir();
		int cacheSize = 10 * 1024 * 1024;
		mOkHttpClient.setCache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
		return okHttpEngine;
	}

	/**
	 * @param currentActivity
	 *            当前活动的activity对象
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 */
	public void getAsynHttp(final BaseActivity currentActivity, final String url) {

		final Request request = new Request.Builder().url(url).build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String str = response.body().string();
				currentActivity.httpResponse(url, null, str);
			}
		});
	}

	public Response getSyncHttp(final BaseActivity currentActivity, final String url) throws IOException {
		// OkHttpClient mOkHttpClient = new OkHttpClient();
		final Request request = new Request.Builder().url(url).build();
		Call call = mOkHttpClient.newCall(request);
		Response mResponse = call.execute();
		if (mResponse.isSuccessful()) {
			currentActivity.httpResponse(url, null, mResponse.body().string());
			return mResponse;
		} else {
			currentActivity.httpError(url, null);
		}
		return null;
	}

	public void postAsynHttp(final BaseActivity currentActivity, final String url, final Map<String, Object> params) {
		// OkHttpClient mOkHttpClient = new OkHttpClient();
		FormEncodingBuilder builer = _getFormBuilder(params);
		Request request = new Request.Builder().url(url).post(builer.build()).build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				currentActivity.httpError(url, params);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				currentActivity.httpResponse(url, params, response.body().string());
			}
		});
	}

	public void fileUpload(final BaseActivity currentActivity, final String url, final Map<String, Object> params, File[] files, String[] fileKeys) {

		Request request = buildMultipartFormRequest(url, files, fileKeys, params);

		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				currentActivity.httpError(url, params);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				String str = response.body().string();
				currentActivity.httpResponse(url, params, response.body().string());
			}
		});
	}

	/**
	 * 下载文件，返回文件绝对路径
	 * 
	 * @param currentActivity
	 * @param url
	 *            文件网络地址
	 * @param destFileDir
	 *            文件存储位置
	 */
	public void downloadFile(final BaseActivity currentActivity, final String url, final String destFileDir) {
		final Request request = new Request.Builder().url(url).build();
		final Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(final Request request, final IOException e) {
				currentActivity.httpError(url, new HashMap<String, Object>());
			}

			@Override
			public void onResponse(Response response) {
				InputStream is = null;
				byte[] buf = new byte[2048];
				int len = 0;
				FileOutputStream fos = null;
				try {
					is = response.body().byteStream();
					File file = new File(destFileDir, getFileName(url));
					fos = new FileOutputStream(file);
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
					}
					fos.flush();
					Map<String, Object> params = new HashMap<String, Object>();
					currentActivity.completeDownload(file.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
					currentActivity.httpError(url, new HashMap<String, Object>(), e);
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (IOException e) {
					}
					try {
						if (fos != null)
							fos.close();
					} catch (IOException e) {
					}
				}

			}
		});
	}

	public void displayImage(final Handler mHandler, final ImageView view, final String imgUrl, final int errorResId) {
		final Request request = new Request.Builder().url(imgUrl).build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						view.setImageResource(errorResId);
					}
				});
			}

			@Override
			public void onResponse(Response response) {
				InputStream is = null;
				try {
					is = response.body().byteStream();
					ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
					ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
					int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
					try {
						is.reset();
					} catch (IOException e) {
						response = _getAsyn(imgUrl);
						is = response.body().byteStream();
					}

					BitmapFactory.Options ops = new BitmapFactory.Options();
					ops.inJustDecodeBounds = false;
					ops.inSampleSize = inSampleSize;
					final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							view.setImageBitmap(bm);
						}
					});
				} catch (Exception e) {
					setErrorResId(mHandler, view, errorResId);

				} finally {
					if (is != null)
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
		});

	}

	/**
	 * 同步的Get请求
	 * 
	 * @param url
	 * @return Response
	 */
	private Response _getAsyn(String url) throws IOException {
		final Request request = new Request.Builder().url(url).build();
		Call call = mOkHttpClient.newCall(request);
		Response execute = call.execute();
		return execute;
	}

	private FormEncodingBuilder _getFormBuilder(Map<String, Object> params) {
		FormEncodingBuilder builder = new FormEncodingBuilder();
		if (params != null && params.size() > 0) {
			Set<Entry<String, Object>> set = params.entrySet();
			Iterator<Entry<String, Object>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				builder.add(entry.getKey(), entry.getValue().toString());
			}
		}
		return builder;
	}

	private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, final Map<String, Object> params) {

		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

		if (params != null && params.size() > 0) {
			Set<Entry<String, Object>> set = params.entrySet();
			Iterator<Entry<String, Object>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				builder.addPart(Headers.of(String.format("Content-Disposition", "form-data; name=\"%s\"", entry.getKey())), RequestBody.create(null, entry.getValue().toString()));
			}
		}

		if (files != null) {
			RequestBody fileBody = null;
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String fileName = file.getName();
				fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
				// TODO 根据文件名设置contentType
				builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""), fileBody);
			}
		}

		RequestBody requestBody = builder.build();
		return new Request.Builder().url(url).post(requestBody).build();
	}

	private String guessMimeType(String path) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentTypeFor = fileNameMap.getContentTypeFor(path);
		if (contentTypeFor == null) {
			contentTypeFor = "application/octet-stream";
		}
		return contentTypeFor;
	}

	private String getFileName(String path) {
		int separatorIndex = path.lastIndexOf("/");
		return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
	}

	private void setErrorResId(final Handler mHandler, final ImageView view, final int errorResId) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				view.setImageResource(errorResId);
			}
		});
	}
}
