/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.distribution.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.librairy.computing.helper.StorageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource(properties = {
        "librairy.computing.fs = hdfs://hadoop-server-ip:8020"
})
public class HDFSStorageTest {

    /**
     * set HADOOP_USER_NAME for testing
     */

    private static final Logger LOG = LoggerFactory.getLogger(HDFSStorageTest.class);

    @Autowired
    StorageHelper storageHelper;

    @Test
    public void save(){

        File file = new File("target/librairy-dependencies.jar");

        System.out.println(file.exists());

        storageHelper.save("/librairy/lib/librairy-dependencies.jar", file);

    }


    @Test
    public void deleteIfExists(){

        storageHelper.deleteIfExists("/librairy/lib/librairy-dependencies.jar");

    }

    @Test
    public void read() throws IOException, URISyntaxException {

        File file = storageHelper.read("/librairy/domains/4f56ab24bb6d815a48b8968a3b157470/stopwords.txt");

        LOG.info("File Path: " + file);

        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        System.out.println(content);

    }


}