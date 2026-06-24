package com.nemanja.shipmenttrackingsystem.importfile;

import com.nemanja.shipmenttrackingsystem.importfile.dto.ImportResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ShipmentImportService shipmentImportService;

    @PostMapping(value = "/shipments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResultResponse importShipments(@RequestPart("file") MultipartFile file) {
        return shipmentImportService.importShipments(file);
    }
}