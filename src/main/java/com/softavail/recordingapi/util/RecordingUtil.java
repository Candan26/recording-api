package com.softavail.recordingapi.util;

import java.util.Arrays;

public class RecordingUtil {
    public static final String FAILED = "1";
    public static final  String SUCCEED = "0";
    public static final String[] EXTENSION_LIST ={"PNG","png", "jpeg", "jpg", "docx", "pdf", "xlsx"};
    public static final String INSERTED = "FILE INSERTED";
    public static final String DELETED = "FILE DELETED";
    public static final String UPDATED = "FILE UPDATED";
    public static final String TEST = "FILE FOR TEST";

    public static final String ERROR_UNPROCESSABLE_EXTENSION = "The extension could only be " + Arrays.toString(RecordingUtil.EXTENSION_LIST);
    public static final String ERROR_UNPROCESSABLE_FILE_BRACE = "The file cannot find in this params please insert a file name or id";
    public static final String ERROR_FILE_NOT_IN_DB = "The file does not exist in Db please check  with valid id or name";
    public static final String ERROR_NO_CONTENT_TABLE = "Table does not have any element";
}