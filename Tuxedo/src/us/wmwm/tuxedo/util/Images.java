package us.wmwm.tuxedo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

public class Images {
	
	public static File resizeImage(Context ctx, int maxWidth, int maxHeight, int quality, Number prefix, Uri uri) {
		File mediaStorageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			mediaStorageDir.mkdirs();
		File file = new File(mediaStorageDir, prefix+".jpg");
		if(file.exists()) {
			return file;
		}
		Bitmap b = loadImage(ctx, maxWidth, maxHeight, uri);
		
		if(b!=null) {
						
			FileOutputStream fos = null;			
			try {
				fos = new FileOutputStream(file);
				b.compress(CompressFormat.JPEG, quality, fos);				
			} catch (Exception e) {
				file = null;
			} finally {
				b.recycle();
			}
			
			return file;			
		}
		
		return null;
	}
	
	public static BitmapFactory.Options getImageSize(Context ctx, Uri uri) {
		if (uri.getScheme().equals("file")) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			String path = uri.getPath();
			BitmapFactory.decodeFile(path, o);
			return o;
		} else {
			InputStream is = null;
			try {
				is = ctx.getContentResolver().openInputStream(uri);
			} catch (FileNotFoundException e) {
				return null;
			}
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is,null, o);			
			try {
				is.close();
				is = ctx.getContentResolver().openInputStream(uri);
			} catch (Exception e) {
				return o;
			}
		}
		return null;
	}

	public static Bitmap loadImage(Context ctx, int maxWidth, int maxHeight, Uri uri) {
		if (uri.getScheme().equals("file")) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			String path = uri.getPath();
			BitmapFactory.decodeFile(path, o);
			int scale = 0;
			int newWidth = o.outWidth;
			int newHeight = o.outHeight;
			while (newWidth > maxWidth || newHeight > maxHeight) {
				scale += 1;
				newWidth = newWidth/2;
				newHeight = newHeight/2;
			}
			o.inJustDecodeBounds = false;
			o.inSampleSize = scale;
			final Bitmap b = BitmapFactory.decodeFile(
					path, o);
			return b;
		}
		if(uri.getScheme().equals("content")) {
			InputStream is = null;
			try {
				is = ctx.getContentResolver().openInputStream(uri);
			} catch (FileNotFoundException e) {
				return null;
			}
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is,null, o);			
			try {
				is.close();
				is = ctx.getContentResolver().openInputStream(uri);
			} catch (Exception e) {
				return null;
			}
			int scale = 0;
			int newWidth = o.outWidth;
			int newHeight = o.outHeight;
			while (newWidth > maxWidth || newHeight > maxHeight) {
				scale += 1;
				newWidth = newWidth/2;
				newHeight = newHeight/2;
			}
			o.inJustDecodeBounds = false;
			o.inSampleSize = scale;
			final Bitmap b = BitmapFactory.decodeStream(
					is, null, o);
			return b;
		}
		return null;
	}
			
}
