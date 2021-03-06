package com.sonetag;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * This class allows to get document path from uri
 * @version 1
 * @author Benjamin BOURG
 */
public class UriToPath {

    /**
     * App context
     */
    private Context context;

    /**
     * Constructor
     * @param context app context
     */
    public UriToPath(Context context) {
        this.context = context;
    }

    /**
     * Get the document path from uri
     * @param uri uri of the document
     * @return the path of the document
     */
    public String getFilePathByUri(Uri uri) {
        String path = null;
        // start with file://
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // The ones beginning with /storage are also returned directly
        if (isOtherDocument(uri)) {
            path = uri.getPath();
            return path;
        }
        // Start with content://, such as content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            if (path != null) return path;
        }
        // 4.4 and later start with content://, such as content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    //When downloading content providers, you should determine whether the download manager is disabled
                    int stateCode = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
                    if (stateCode != 0 && stateCode != 1) {
                        return null;
                    }
                    final String id = DocumentsContract.getDocumentId(uri);

                    if (id != null && id.startsWith("msf:")) {
                        final File file = new File(context.getCacheDir(), "temp_file" + Objects.requireNonNull(context.getContentResolver().getType(uri)).split("/")[1]);
                        try (final InputStream inputStream = context.getContentResolver().openInputStream(uri); OutputStream output = new FileOutputStream(file)) {
                            final byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }

                            output.flush();
                            Log.i("TAG", "getFilePathByUri: "+file.getPath());
                            return file.getPath();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }else {
                        final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        path = getDataColumn(contentUri, null, null);
                        return path;
                    }
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isOtherDocument(Uri uri) {
        // The ones beginning with /storage are also returned directly
        if (uri != null && uri.getPath() != null) {
            String path = uri.getPath();
            if (path.startsWith("/storage")) {
                return true;
            }
            if (path.startsWith("/external_files")) {
                return true;
            }
        }
        return false;
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}