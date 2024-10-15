package com.example.demo.controller;

import com.example.demo.service.DeviceControlService;
import com.example.demo.service.DeviceDataService;
import com.example.demo.util.Response;
import com.example.demo.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//

@RestController
public class DeviceController {
    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);

    private final DeviceDataService deviceDataService;
    private final DeviceControlService deviceControlService;

    public DeviceController(
            DeviceDataService deviceDataService,
            DeviceControlService deviceControlService
    ) {
        this.deviceDataService = deviceDataService;
        this.deviceControlService = deviceControlService;
    }

    @ResponseBody
    @GetMapping("/device/{id}/{ops}")
    public Result lightOps(@PathVariable String id, @PathVariable String ops) {
        if (deviceControlService.deviceOps(id, ops)) {
            return Result.Ok();
        }
        return Result.Err("err");
    }

    @ResponseBody
    @GetMapping("/device/list")
    public Response<List<String>> list() {
        return Response.success(deviceDataService.listDevices());
    }

    @ResponseBody
    @GetMapping("/device/connect/{ip}")
    public Result connect(@PathVariable String ip) {
        if (deviceControlService.connect(ip)) {
            return Result.Ok();
        }
        return Result.Err("err");
    }
}

