package com.kb;

import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.thingshape.ThingShape;
		
		
@ThingworxPropertyDefinitions(properties = {
		@ThingworxPropertyDefinition(name = "OrdersRepositoryPath", description = "Orders Repository Path", baseType = "STRING", category = "Fastems", aspects = {
		"isPersistent:true" }),
		@ThingworxPropertyDefinition(name = "TransferOrders", description = "Transfer orders", baseType = "BOOLEAN", category = "Fastems", aspects = {
				"isPersistent:true", "defaultValue:false" }),
		@ThingworxPropertyDefinition(name = "TransferMessages", description = "Transfer messages", baseType = "BOOLEAN", category = "Fastems", aspects = {
				"isPersistent:true", "defaultValue:false" }),
		@ThingworxPropertyDefinition(name = "LastImportStart", description = "Last start of Fastems Import", baseType = "DATETIME", category = "Fastems", aspects = {
				"isPersistent:true" }),
		@ThingworxPropertyDefinition(name = "LastImportEnd", description = "Last end of Fastems Import", baseType = "DATETIME", category = "Fastems", aspects = {
				"isPersistent:true" }),
		@ThingworxPropertyDefinition(name = "ImportStatus", description = "Fastems --> TWX", baseType = "STRING", category = "Fastems", aspects = {
				"isPersistent:true" }),
		@ThingworxPropertyDefinition(name = "LastExportStart", description = "Last start of Fastems Export", baseType = "DATETIME", category = "Fastems", aspects = {
		"isPersistent:true" }),
		@ThingworxPropertyDefinition(name = "LastExportEnd", description = "Last end of Fastems Export", baseType = "DATETIME", category = "Fastems", aspects = {
		"isPersistent:true" }),
		@ThingworxPropertyDefinition(name = "ExportStatus", description = "TWX --> Fastems", baseType = "STRING", category = "Fastems", aspects = {
		"isPersistent:true" })})

public class KBFastemsThingShape extends ThingShape {


	public KBFastemsThingShape() {
		// TODO Auto-generated constructor stub
	}

}
