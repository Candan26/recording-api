package com.softavail.recordingapi.util;

public class RecordingUtil {
    public static final String FAILED = "1";
    public static final  String SUCCEED = "0";
    public static final String INSERTED = "FILE INSERTED";
    public static final String DELETED = "FILE DELETED";
    public static final String UPDATED = "FILE UPDATED";

    public static final String ERROR_UNPROCESSABLE_EXTENSION = "The extension is not valid please shar valid extension";
    public static final String ERROR_UNPROCESSABLE_FILE_BRACE = "The file cannot find in this params please insert a file name or id";
    public static final String ERROR_FILE_NOT_IN_DB = "The file does not exist in Db please check  with valid id or name";
    public static final String ERROR_NO_CONTENT_TABLE = "Table does not have any element";
    public static final String ERROR_URL_ENDPOINT = "URI is not absolute";
    public static final String ERROR_PARSING_EXCEPTION = "The object cannot parse to json format";
    public static final String ERROR_URI_CREATING= "The uri  given string violates RFC 2396";
    public static final String ERROR_CONNECTION_HTTP = "The file cannot sent via http request please check params";
    public static final String boundary =  "*****";
    public static final String crlf = "\r\n";
    public static final String twoHyphens = "--";

}
