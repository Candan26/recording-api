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
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
@Api(value = "Controller block for recording objects")
@Slf4j
public class RecordController {

    private final RecordService recordService;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/upload")
    @ApiOperation(value = "Upload Image on server")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File saved on embedded db"),
            @ApiResponse(code = 422, message = "If extension is not correct."),
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
    @PostMapping(value = "/update")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File is deleted on embedded database"),
            @ApiResponse(code = 422, message = "If file payload is too large"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> updateFile(@RequestPart("mediaFile") MultipartFile mediaFile,
                                        @RequestPart("metadata")  Webhook metadata) {
        try {
            RecordResponse response = recordService.updateFile(mediaFile,metadata);
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
    @RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE,value = "/delete")
    @ApiOperation(value = "Delete file on server")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File is deleted on embedded database"),
            @ApiResponse(code = 422, message = "If file is not found on database"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> deleteFile(@RequestBody Webhook deleteRequest) {
        try {
            RecordResponse response = recordService.deleteFile(deleteRequest);
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
    @GetMapping(value = "/getAll")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File is deleted on embedded database"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> getAllFile() {
        try {
            RecordResponse response = recordService.getAllFileInfo();
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
    @GetMapping(value = "/findByCallId/{id}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File is deleted on embedded database"),
            @ApiResponse(code = 413, message = "If file is not found on database"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> findByCallId(@PathVariable String id) {
        try {
            RecordResponse response = recordService.getFileByCallId(id);
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
    @GetMapping(value = "/findById/{id}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File is deleted on embedded database"),
            @ApiResponse(code = 413, message = "If file is not found on database"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> findById(@PathVariable String id) {
        try {
            RecordResponse response = recordService.getFileById(id);
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
    @GetMapping(value = "/getAll/{page}/{size}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File is deleted on embedded database"),
            @ApiResponse(code = 413, message = "If search returns null in db"),
            @ApiResponse(code = 500, message = "If storage service get exception.")})
    public ResponseEntity<?> getAllFileByPage(@PathVariable int page, @PathVariable int size, @And({
            @Spec(path = "filename", params = "filename", spec = Equal.class),
            @Spec(path = "fileExtension", params = "fileExtension", spec = Equal.class)
    }) Specification<Webhook> spec) {
        try {
            RecordResponse response = recordService.queryFile(spec,page,size);
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
