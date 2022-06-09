package com.softavail.recordingapi.controller;

import com.softavail.recordingapi.entity.Webhook;
import com.softavail.recordingapi.model.RecordResponse;
import com.softavail.recordingapi.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
@Api(value = "Controller block for storage objects")
@Slf4j
public class RecordController {

    private final RecordService recordService;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/upload")
    @ApiOperation(value = "Upload Image on server")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File saved on embedded db"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> uploadFile(@RequestPart("mediaFile") MultipartFile mediaFile,
                                        @RequestPart("metadata")  Webhook metadata) {
        try {
            RecordResponse response = recordService.uploadFile(mediaFile, metadata);
            if (response.getError() != null) {
                return new ResponseEntity<>(response, response.getError().getStatus());
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Exception on ", ex);
            return new ResponseEntity<>("Service Error " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE,value = "/delete/{callId}")
    @ApiOperation(value = "Delete file on server")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File is deleted on embedded database"),
            @ApiResponse(code = 422, message = "If file is not found on database"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> deleteFile(@PathVariable String callId) {
        try {
            RecordResponse response = recordService.deleteFile(callId);
            if (response.getError() != null) {
                return new ResponseEntity<>(response, response.getError().getStatus());
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Exception on ", ex);
            return new ResponseEntity<>("Service Error " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
