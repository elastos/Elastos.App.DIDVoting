package org.elastos.service;

import org.elastos.pojo.DidProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElaServiceComponentTest {
    @Autowired
    ElaServiceComponent elaServiceComponent;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void getVoteInfoTest() throws Exception {
        List<DidProperty> propertyList = elaServiceComponent.getVoteInfo(229269L);
    }

}