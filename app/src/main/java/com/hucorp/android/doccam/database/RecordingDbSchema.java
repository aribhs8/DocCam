package com.hucorp.android.doccam.database;

public class RecordingDbSchema
{
    public static final class RecordingTable
    {
        public static final String NAME = "recordings";

        public static final class Cols
        {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DURATION = "duration";
        }
    }
}
