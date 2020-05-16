package io.github.vhow.finder.file;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.gmail.dailyefforts.filemanager.BuildConfig;
import com.gmail.dailyefforts.filemanager.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHelper {
    private static final String TAG = "FileHelper";

    private static final Map<String, Integer> NAME_ICON_MAP = new HashMap<>();

    static {
        NAME_ICON_MAP.put("3dm", R.drawable.e_3dm);
        NAME_ICON_MAP.put("3ds", R.drawable.e_3ds);
        NAME_ICON_MAP.put("3g2", R.drawable.e_3g2);
        NAME_ICON_MAP.put("3gp", R.drawable.e_3gp);
        NAME_ICON_MAP.put("7z", R.drawable.e_7z);
        NAME_ICON_MAP.put("aac", R.drawable.e_aac);
        NAME_ICON_MAP.put("ai", R.drawable.e_ai);
        NAME_ICON_MAP.put("aif", R.drawable.e_aif);
        NAME_ICON_MAP.put("apk", R.drawable.e_apk);
        NAME_ICON_MAP.put("app", R.drawable.e_app);
        NAME_ICON_MAP.put("asf", R.drawable.e_asf);
        NAME_ICON_MAP.put("asp", R.drawable.e_asp);
        NAME_ICON_MAP.put("aspx", R.drawable.e_aspx);
        NAME_ICON_MAP.put("asx", R.drawable.e_asx);
        NAME_ICON_MAP.put("avi", R.drawable.e_avi);
        NAME_ICON_MAP.put("bak", R.drawable.e_bak);
        NAME_ICON_MAP.put("bat", R.drawable.e_bat);
        NAME_ICON_MAP.put("bin", R.drawable.e_bin);
        NAME_ICON_MAP.put("blank", R.drawable.e_blank);
        NAME_ICON_MAP.put("bmp", R.drawable.e_bmp);
        NAME_ICON_MAP.put("cab", R.drawable.e_cab);
        NAME_ICON_MAP.put("cad", R.drawable.e_cad);
        NAME_ICON_MAP.put("cdr", R.drawable.e_cdr);
        NAME_ICON_MAP.put("cer", R.drawable.e_cer);
        NAME_ICON_MAP.put("cfg", R.drawable.e_cfg);
        NAME_ICON_MAP.put("cfm", R.drawable.e_cfm);
        NAME_ICON_MAP.put("cgi", R.drawable.e_cgi);
        NAME_ICON_MAP.put("class", R.drawable.e_class);
        NAME_ICON_MAP.put("com", R.drawable.e_com);
        NAME_ICON_MAP.put("cpl", R.drawable.e_cpl);
        NAME_ICON_MAP.put("cpp", R.drawable.e_cpp);
        NAME_ICON_MAP.put("crx", R.drawable.e_crx);
        NAME_ICON_MAP.put("csr", R.drawable.e_csr);
        NAME_ICON_MAP.put("css", R.drawable.e_css);
        NAME_ICON_MAP.put("csv", R.drawable.e_csv);
        NAME_ICON_MAP.put("cue", R.drawable.e_cue);
        NAME_ICON_MAP.put("cur", R.drawable.e_cur);
        NAME_ICON_MAP.put("dat", R.drawable.e_dat);
        NAME_ICON_MAP.put("db", R.drawable.e_db);
        NAME_ICON_MAP.put("dbf", R.drawable.e_dbf);
        NAME_ICON_MAP.put("dds", R.drawable.e_dds);
        NAME_ICON_MAP.put("debian", R.drawable.e_debian);
        NAME_ICON_MAP.put("dem", R.drawable.e_dem);
        NAME_ICON_MAP.put("dll", R.drawable.e_dll);
        NAME_ICON_MAP.put("dmg", R.drawable.e_dmg);
        NAME_ICON_MAP.put("dmp", R.drawable.e_bmp);
        NAME_ICON_MAP.put("doc", R.drawable.e_doc);
        NAME_ICON_MAP.put("docx", R.drawable.e_docx);
        NAME_ICON_MAP.put("drv", R.drawable.e_drv);
        NAME_ICON_MAP.put("dtd", R.drawable.e_dtd);
        NAME_ICON_MAP.put("dwg", R.drawable.e_dwg);
        NAME_ICON_MAP.put("dxf", R.drawable.e_dxf);
        NAME_ICON_MAP.put("elf", R.drawable.e_elf);
        NAME_ICON_MAP.put("eml", R.drawable.e_eml);
        NAME_ICON_MAP.put("eps", R.drawable.e_eps);
        NAME_ICON_MAP.put("exe", R.drawable.e_exe);
        NAME_ICON_MAP.put("fla", R.drawable.e_fla);
        NAME_ICON_MAP.put("flash", R.drawable.e_flash);
        NAME_ICON_MAP.put("flv", R.drawable.e_flv);
        NAME_ICON_MAP.put("fnt", R.drawable.e_fnt);
        NAME_ICON_MAP.put("fon", R.drawable.e_fon);
        NAME_ICON_MAP.put("gam", R.drawable.e_gam);
        NAME_ICON_MAP.put("gbr", R.drawable.e_gbr);
        NAME_ICON_MAP.put("ged", R.drawable.e_ged);
        NAME_ICON_MAP.put("gif", R.drawable.e_gif);
        NAME_ICON_MAP.put("gpx", R.drawable.e_gpx);
        NAME_ICON_MAP.put("gz", R.drawable.e_gz);
        NAME_ICON_MAP.put("gzip", R.drawable.e_gzip);
        NAME_ICON_MAP.put("hqz", R.drawable.e_hqz);
        NAME_ICON_MAP.put("html", R.drawable.e_html);
        NAME_ICON_MAP.put("ibooks", R.drawable.e_ibooks);
        NAME_ICON_MAP.put("icns", R.drawable.e_icns);
        NAME_ICON_MAP.put("ico", R.drawable.e_ico);
        NAME_ICON_MAP.put("ics", R.drawable.e_ics);
        NAME_ICON_MAP.put("iff", R.drawable.e_iff);
        NAME_ICON_MAP.put("indd", R.drawable.e_indd);
        NAME_ICON_MAP.put("ipa", R.drawable.e_ipa);
        NAME_ICON_MAP.put("iso", R.drawable.e_iso);
        NAME_ICON_MAP.put("jar", R.drawable.e_jar);
        NAME_ICON_MAP.put("jpg", R.drawable.e_jpg);
        NAME_ICON_MAP.put("js", R.drawable.e_js);
        NAME_ICON_MAP.put("jsp", R.drawable.e_jsp);
        NAME_ICON_MAP.put("key", R.drawable.e_key);
        NAME_ICON_MAP.put("kml", R.drawable.e_kml);
        NAME_ICON_MAP.put("kmz", R.drawable.e_kmz);
        NAME_ICON_MAP.put("lnk", R.drawable.e_lnk);
        NAME_ICON_MAP.put("log", R.drawable.e_log);
        NAME_ICON_MAP.put("lua", R.drawable.e_lua);
        NAME_ICON_MAP.put("m3u", R.drawable.e_m3u);
        NAME_ICON_MAP.put("m4a", R.drawable.e_m4a);
        NAME_ICON_MAP.put("m4v", R.drawable.e_m4v);
        NAME_ICON_MAP.put("mach", R.drawable.e_mach);
        NAME_ICON_MAP.put("max", R.drawable.e_max);
        NAME_ICON_MAP.put("mdb", R.drawable.e_mdb);
        NAME_ICON_MAP.put("mdf", R.drawable.e_mdf);
        NAME_ICON_MAP.put("mid", R.drawable.e_mid);
        NAME_ICON_MAP.put("mim", R.drawable.e_mim);
        NAME_ICON_MAP.put("mov", R.drawable.e_mov);
        NAME_ICON_MAP.put("mp3", R.drawable.e_mp3);
        NAME_ICON_MAP.put("mp4", R.drawable.e_mp4);
        NAME_ICON_MAP.put("mpa", R.drawable.e_mpa);
        NAME_ICON_MAP.put("mpg", R.drawable.e_msg);
        NAME_ICON_MAP.put("msg", R.drawable.e_msg);
        NAME_ICON_MAP.put("msi", R.drawable.e_msi);
        NAME_ICON_MAP.put("nes", R.drawable.e_nes);
        NAME_ICON_MAP.put("object", R.drawable.e_object);
        NAME_ICON_MAP.put("odb", R.drawable.e_odb);
        NAME_ICON_MAP.put("odc", R.drawable.e_odc);
        NAME_ICON_MAP.put("odf", R.drawable.e_odf);
        NAME_ICON_MAP.put("odg", R.drawable.e_odg);
        NAME_ICON_MAP.put("odi", R.drawable.e_odi);
        NAME_ICON_MAP.put("odp", R.drawable.e_odp);
        NAME_ICON_MAP.put("ods", R.drawable.e_ods);
        NAME_ICON_MAP.put("odt", R.drawable.e_odt);
        NAME_ICON_MAP.put("odx", R.drawable.e_odx);
        NAME_ICON_MAP.put("ogg", R.drawable.e_ogg);
        NAME_ICON_MAP.put("otf", R.drawable.e_otf);
        NAME_ICON_MAP.put("pages", R.drawable.e_pages);
        NAME_ICON_MAP.put("pct", R.drawable.e_pct);
        NAME_ICON_MAP.put("pdb", R.drawable.e_pdb);
        NAME_ICON_MAP.put("pdf", R.drawable.e_pdf);
        NAME_ICON_MAP.put("pif", R.drawable.e_pif);
        NAME_ICON_MAP.put("pkg", R.drawable.e_pkg);
        NAME_ICON_MAP.put("pl", R.drawable.e_pl);
        NAME_ICON_MAP.put("png", R.drawable.e_png);
        NAME_ICON_MAP.put("pps", R.drawable.e_pps);
        NAME_ICON_MAP.put("ppt", R.drawable.e_ppt);
        NAME_ICON_MAP.put("pptx", R.drawable.e_pptx);
        NAME_ICON_MAP.put("ps", R.drawable.e_ps);
        NAME_ICON_MAP.put("psd", R.drawable.e_psd);
        NAME_ICON_MAP.put("pub", R.drawable.e_pub);
        NAME_ICON_MAP.put("py", R.drawable.e_py);
        NAME_ICON_MAP.put("ra", R.drawable.e_ra);
        NAME_ICON_MAP.put("rar", R.drawable.e_rar);
        NAME_ICON_MAP.put("raw", R.drawable.e_raw);
        NAME_ICON_MAP.put("rm", R.drawable.e_rm);
        NAME_ICON_MAP.put("rom", R.drawable.e_rom);
        NAME_ICON_MAP.put("rpm", R.drawable.e_rpm);
        NAME_ICON_MAP.put("rss", R.drawable.e_rss);
        NAME_ICON_MAP.put("rtf", R.drawable.e_rtf);
        NAME_ICON_MAP.put("sav", R.drawable.e_sav);
        NAME_ICON_MAP.put("sdf", R.drawable.e_sdf);
        NAME_ICON_MAP.put("sitx", R.drawable.e_sitx);
        NAME_ICON_MAP.put("sql", R.drawable.e_sql);
        NAME_ICON_MAP.put("srt", R.drawable.e_srt);
        NAME_ICON_MAP.put("svg", R.drawable.e_svg);
        NAME_ICON_MAP.put("swf", R.drawable.e_swf);
        NAME_ICON_MAP.put("sys", R.drawable.e_sys);
        NAME_ICON_MAP.put("tar", R.drawable.e_tar);
        NAME_ICON_MAP.put("tex", R.drawable.e_tex);
        NAME_ICON_MAP.put("tga", R.drawable.e_tga);
        NAME_ICON_MAP.put("thm", R.drawable.e_thm);
        NAME_ICON_MAP.put("tiff", R.drawable.e_tiff);
        NAME_ICON_MAP.put("tmp", R.drawable.e_tmp);
        NAME_ICON_MAP.put("torrent", R.drawable.e_torrent);
        NAME_ICON_MAP.put("ttf", R.drawable.e_ttf);
        NAME_ICON_MAP.put("txt", R.drawable.e_txt);
        NAME_ICON_MAP.put("uue", R.drawable.e_uue);
        NAME_ICON_MAP.put("vb", R.drawable.e_vb);
        NAME_ICON_MAP.put("vcd", R.drawable.e_vcd);
        NAME_ICON_MAP.put("vcf", R.drawable.e_vcf);
        NAME_ICON_MAP.put("vob", R.drawable.e_vob);
        NAME_ICON_MAP.put("wav", R.drawable.e_wav);
        NAME_ICON_MAP.put("wma", R.drawable.e_wma);
        NAME_ICON_MAP.put("wmv", R.drawable.e_wmv);
        NAME_ICON_MAP.put("wpd", R.drawable.e_wpd);
        NAME_ICON_MAP.put("wps", R.drawable.e_wps);
        NAME_ICON_MAP.put("wsf", R.drawable.e_wsf);
        NAME_ICON_MAP.put("xhtml", R.drawable.e_xhtml);
        NAME_ICON_MAP.put("xlr", R.drawable.e_xlr);
        NAME_ICON_MAP.put("xls", R.drawable.e_xls);
        NAME_ICON_MAP.put("xlsx", R.drawable.e_xlsx);
        NAME_ICON_MAP.put("xml", R.drawable.e_xml);
        NAME_ICON_MAP.put("yuv", R.drawable.e_yuv);
        NAME_ICON_MAP.put("zip", R.drawable.e_zip);
    }


    private FileHelper() {

    }

    static int getIconResId(String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        if (extension == null) {
            return R.drawable.e_blank;
        }
        final String lowerCase = extension.toLowerCase();
        if (NAME_ICON_MAP.containsKey(lowerCase)) {
            return NAME_ICON_MAP.get(lowerCase);
        }
        return R.drawable.e_blank;
    }

    static boolean isApk(String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        return extension != null && extension.toLowerCase().equals("apk");
    }

    static boolean isImage(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        if (extension == null) {
            return false;
        }
        extension = extension.toLowerCase();
        return extension.equals("png") || extension.equals("jpg") || extension.equals("gif");
    }

    public static String getMimeType(String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Log.d(TAG, "getMimeType: fileName: " + fileName +
                ", extension: " + extension +
                ", mimeType: " + mimeType);
        if (mimeType == null) {
            return "?";
        }
        return mimeType;
    }

    private static File detectSdCardPath() {
        final File storageDir = Environment.getExternalStorageDirectory();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "detectSdCardPath: storageDir " + storageDir);
        }
        File tmpDir = storageDir;
        File storageRoot = storageDir;
        while (tmpDir != null && tmpDir.getParentFile() != null) {
            storageRoot = tmpDir;
            tmpDir = tmpDir.getParentFile();
        }
        Log.d(TAG, "detectSdCardPath: storageRoot " + storageRoot);
        if (storageRoot == null) {
            Log.e(TAG, "detectSdCardPath: storage root is null.");
            return null;
        }
        File[] dirs = storageRoot.listFiles();
        if (dirs == null) {
            return null;
        }
        for (File dir : dirs) {
            if (!dir.exists()) {
                continue;
            }
            if (!dir.canRead()) {
                continue;
            }
            try {
                if (Environment.isExternalStorageRemovable(dir)) {
                    return dir;
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "detectSdCardPath: " + e);
            }
        }
        return null;
    }

    public static File getSdCardPath(Context context) {
        final File sdCard = detectSdCardPath();
        if (sdCard != null) {
            return sdCard;
        }
        final List<StorageInfo> devices = getStorage(context);
        if (devices == null) {
            return null;
        }
        for (StorageInfo info : devices) {
            String path = info.path.toLowerCase();
            if (path.contains("sdcard")) {
                return new File(info.path);
            }
        }
        return null;
    }

    private static List<StorageInfo> getStorage(Context context) {
        if (context == null) {
            Log.e(TAG, "getStorage: context is null.");
            return null;
        }
        final List<StorageInfo> storageInfoList = new ArrayList<>();
        final StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            getVolumeList.setAccessible(true);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);
            if (null != invokes) {
                StorageInfo info;
                for (Object obj : invokes) {
                    Method getPath = obj.getClass().getMethod("getPath");
                    String path = (String) getPath.invoke(obj);
                    info = new StorageInfo(path);
                    File file = new File(info.path);
                    if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                        Method isRemovable = obj.getClass().getMethod("isRemovable");
                        String state;
                        try {
                            Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                            state = (String) getVolumeState.invoke(storageManager, path);
                            info.state = state;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (info.isMounted()) {
                            info.isRemovable = (Boolean) isRemovable.invoke(obj);
                            storageInfoList.add(info);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getStorage: " + e);
        }

        return storageInfoList;
    }

    private static class StorageInfo {
        public String path;
        String state;
        boolean isRemovable;

        StorageInfo(String path) {
            this.path = path;
        }

        boolean isMounted() {
            return Environment.MEDIA_MOUNTED.equals(state);
        }

        @Override
        public String toString() {
            return "StorageInfo{" +
                    "path='" + path + '\'' +
                    ", state='" + state + '\'' +
                    ", isRemovable=" + isRemovable +
                    '}';
        }
    }

}
