package io.genreadme.http.enums;

public enum ContentType {
	APPLICATION_JSON("application/json"), APPLICATION_XML("application/xml"),
	APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded");

	/*
	 * APPLICATION_OCTET_STREAM("application/octet-stream"),
	 * APPLICATION_PDF("application/pdf"), APPLICATION_ZIP("application/zip"),
	 * 
	 * TEXT_PLAIN("text/plain"), TEXT_HTML("text/html"), TEXT_CSS("text/css"),
	 * TEXT_JAVASCRIPT("text/javascript"), TEXT_CSV("text/csv"),
	 * 
	 * IMAGE_PNG("image/png"), IMAGE_JPEG("image/jpeg"), IMAGE_GIF("image/gif"),
	 * IMAGE_SVG("image/svg+xml"), IMAGE_WEBP("image/webp"),
	 * 
	 * MULTIPART_FORM_DATA("multipart/form-data"),
	 * MULTIPART_MIXED("multipart/mixed");
	 * 
	 */
	private final String mediaType;

	ContentType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getMediaType() {
		return mediaType;
	}

	@Override
	public String toString() {
		return mediaType;
	}
}
