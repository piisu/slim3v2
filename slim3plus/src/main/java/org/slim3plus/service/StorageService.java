package org.slim3plus.service;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.tools.cloudstorage.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import javax.inject.Singleton;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

@Singleton
public class StorageService {
    public GcsInputChannel getReadChannel(GcsFilename file) throws IOException {
        GcsService gcsService = GcsServiceFactory.createGcsService();
        return gcsService.openReadChannel(file, 0);
    }

    public <T extends Serializable> T readObject(GcsFilename file) throws IOException {
        try (GcsInputChannel c = getReadChannel(file);
             InputStream in = Channels.newInputStream(c);
             BufferedInputStream bis = new BufferedInputStream(in)
        ) {
            return (T) SerializationUtils.deserialize(bis);
        }
    }

    public boolean isExists(GcsFilename fileName) throws IOException {
        GcsService gcsService = GcsServiceFactory.createGcsService();
        GcsFileMetadata meta = gcsService.getMetadata(fileName);
        return meta != null;
    }

    public byte[] readBytes(GcsFilename file) throws IOException {
        try (GcsInputChannel c = getReadChannel(file);
             InputStream in = Channels.newInputStream(c);
             BufferedInputStream bis = new BufferedInputStream(in);
        ) {
            return IOUtils.toByteArray(bis);
        }
    }

    public void writeObject(GcsFilename file, Serializable object) throws IOException {
        writeObject(file, object, new GcsFileOptions.Builder()
                .mimeType("application/octet-stream").build());
    }

    public void writeObject(GcsFilename file, Serializable object, GcsFileOptions options) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        SerializationUtils.serialize(object, out);
        writeBytes(file, out.toByteArray(), options);
    }

    public void writeBytes(GcsFilename file, byte[] bytes, GcsFileOptions options) throws IOException {
        GcsServiceFactory.createGcsService().createOrReplace(file, options, ByteBuffer.wrap(bytes));
    }


    public String getDefaultBucketName() {
        return AppIdentityServiceFactory.getAppIdentityService()
                .getDefaultGcsBucketName();
    }

    public void delete(GcsFilename file) throws IOException {
        GcsServiceFactory.createGcsService().delete(file);
    }
}
