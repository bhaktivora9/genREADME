package io.genreadme.utils;

import io.genreadme.model.Platform;

public class PlatformUtils {

	public static Platform identifyThePlatform(String url) {
		if (url == null || url.isBlank()) {
			return Platform.UNKNOWN;
		}

		String lower = url.toLowerCase();

		if (lower.contains("github.com")) {
			return Platform.GITHUB;
		} else if (lower.contains("gitlab.com")) {
			return Platform.GITLAB;
		} else if (lower.contains("bitbucket.org")) {
			return Platform.BITBUCKET;
		}

		return Platform.UNKNOWN;
	}

}
