/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.core.AgriCore;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

/**
 *
 * @author RlonRyan
 */
public class JsonCopier {

	public static Stream<Path> getResources(Class clazz, String path, int depth) throws IOException, URISyntaxException {
		URI uri = clazz.getResource(path).toURI();
		Path myPath;
		if (uri.getScheme().equals("jar")) {
			FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
			myPath = fileSystem.getPath(path);
		} else {
			myPath = Paths.get(uri);
		}
		return Files.walk(myPath, depth < 1 ? 1 : depth);
	}

	public static void copyJarResource(Class clazz, Path resource, Path to) {
		try {
			Files.createDirectories(to.getParent());
			Files.copy(clazz.getResourceAsStream(resource.toString()), to);
		} catch (Exception e) {
			// Oh well.
		}
	}

	public static void listJarPlants() {
		try {
			getResources(JsonCopier.class, "/plants", 10).forEach(AgriCore.getLogger("AgriCraft")::debug);
		} catch (Exception e) {
			AgriCore.getLogger("AgriCraft").trace(e);
		}
	}

	public static void copyPlants(Path destination) {
		try {
			getResources(JsonCopier.class, "/plants", 10).forEach((p) -> {
				if (p.toString().endsWith(".json")) {
					copyJarResource(JsonCopier.class, p, destination.resolve(p.toString().substring(1)));
				}
			});
		} catch (Exception e) {
			// Oh well...
			e.printStackTrace();
		}
	}

}
