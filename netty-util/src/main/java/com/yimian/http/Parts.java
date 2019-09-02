package com.yimian.http;

public class Parts {
    private String path;
    private String query;
    private String ref;

    public Parts(String file) {
        int ind = file.indexOf('#');
        ref = ind < 0 ? null : file.substring(ind + 1);
        file = ind < 0 ? file : file.substring(0, ind);
        int q = file.lastIndexOf('?');
        if(q != -1) {
            query = file.substring(q + 1);
            path = file.substring(0, q);
        }
        else {
            path = file;
        }
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getRef() {
        return ref;
    }
}
