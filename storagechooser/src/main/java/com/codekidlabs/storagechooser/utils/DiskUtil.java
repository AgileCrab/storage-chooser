package com.codekidlabs.storagechooser.utils;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.fragments.SecondaryChooserFragment;
import com.codekidlabs.storagechooser.models.Config;

import java.io.File;

public class DiskUtil {

    public static final String IN_KB = "KiB";
    public static final String IN_MB = "MiB";
    public static final String IN_GB = "GiB";
    public static final String SC_PREFERENCE_KEY = "storage_chooser_path";
    public static java.lang.String SC_CHOOSER_FLAG = "storage_chooser_type";
    private static final String SD_CARD_1_PATH = "/mnt/external_sd1";
    private static final String SD_CARD_2_PATH = "/mnt/external_sd";
    private static final String USB_BASE_PATH_PART_1 = "mnt/usb_storage/USB_DISK"; // "/mnt/usb_storage/USB_DISK2/udisk0/"
    private static final String USB_BASE_PATH_PART_2 = "/udisk0";

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static void saveChooserPathPreference(SharedPreferences sharedPreferences, String path) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SC_PREFERENCE_KEY, path);
            editor.apply();
        } catch (NullPointerException e) {
            Log.e("StorageChooser", "No sharedPreference was supplied. Supply sharedPreferencesObject via withPreference() or disable saving with actionSave(false)");
        }
    }

    public static boolean isLollipopAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * secondary choosers are dialogs apart from overview (CustomChooserFragment and FilePickerFragment)
     * Configs :-
     * setType()
     * allowCustomPath()
     *
     * @param dirPath root path(starting-point) for the secondary choosers
     * @param config  configuration from developer
     */

    public static void showSecondaryChooser(String dirPath, Config config) {

        Bundle bundle = new Bundle();
        bundle.putString(DiskUtil.SC_PREFERENCE_KEY, dirPath);

        switch (config.getSecondaryAction()) {
            case StorageChooser.NONE:
                break;
            case StorageChooser.DIRECTORY_CHOOSER:
                bundle.putBoolean(DiskUtil.SC_CHOOSER_FLAG, false);
                SecondaryChooserFragment c = new SecondaryChooserFragment();
                c.setArguments(bundle);
                c.show(config.getFragmentManager(), "custom_chooser");
                break;
            case StorageChooser.FILE_PICKER:
                bundle.putBoolean(DiskUtil.SC_CHOOSER_FLAG, true);
                SecondaryChooserFragment f = new SecondaryChooserFragment();
                f.setArguments(bundle);
                f.show(config.getFragmentManager(), "file_picker");
                break;
        }
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isSdCard1Exist() {
        if (isSdCardExist() && isFileExists(SD_CARD_1_PATH)) {
            File file = new File(SD_CARD_1_PATH, "Temp");
            if (createDirectory(file)) {
                file.delete();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean isSdCard2Exist() {
        if (isSdCardExist() && isFileExists(SD_CARD_2_PATH)) {
            File file = new File(SD_CARD_2_PATH, "Temp");
            if (createDirectory(file)) {
                file.delete();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean isFileExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        try {
            File file = new File(filePath);
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean createDirectory(File dir) {
        return !(dir == null || dir.isFile()) && (dir.isDirectory() || dir.mkdirs());
    }

    public static String getSdCard1Path() {
        return SD_CARD_1_PATH;
    }

    public static String getSdCard2Path() {
        return SD_CARD_2_PATH;
    }

    public static String getUSBPath() {
        return getMobileFlashDrivePath();
    }

    public static boolean isUsbExist() {
        return getMobileFlashDrivePath() != null;
    }

    private static String getMobileFlashDrivePath() {
        for (int i = 0; i < 6; i++) {
            String path = USB_BASE_PATH_PART_1 + i + USB_BASE_PATH_PART_2;
            if (isFileExists(path)) {
                File file = new File(path, "Temp");
                if (createDirectory(file)) {
                    file.delete();
                    return path;
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
