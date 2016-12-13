package com.example.jose.updated.controller;

import com.example.jose.updated.model.Page;

import java.util.List;

/**
 * Created by Joe on 12/8/16.
 */
//TODO this is called when updatedPages is > 0
public interface UpdatedCallback {
    void onUpdateDetected(List<Page> updatedPagesList);

}