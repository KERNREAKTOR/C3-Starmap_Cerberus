module net.clanwolf.starmap.transfer {
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires java.sql;

	exports net.clanwolf.starmap.transfer;
	exports net.clanwolf.starmap.transfer.enums;
	exports net.clanwolf.starmap.transfer.dtos;
	exports net.clanwolf.starmap.transfer.enums.roleplayinputdatatypes;
	exports net.clanwolf.starmap.transfer.enums.catalogObjects to com.fasterxml.jackson.databind;

	opens net.clanwolf.starmap.transfer.enums.catalogObjects to com.fasterxml.jackson.databind;
	exports net.clanwolf.starmap.transfer.util;
	exports net.clanwolf.starmap.constants;
}
