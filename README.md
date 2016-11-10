Zucchini
========

[![Join the chat at https://gitter.im/Comcast/zucchini](https://badges.gitter.im/Comcast/zucchini.svg)](https://gitter.im/Comcast/zucchini?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/Comcast/zucchini.svg)](https://travis-ci.org/Comcast/zucchini)

[http://comcast.github.io/zucchini/](http://comcast.github.io/zucchini/)

##Summary

Zucchini sits on top of cucumber-jvm and maven to provide higher testing throughput by enabling concurrent test execution. A test author need only specify a description of each of the devices (or browsers) under test as well as the cucumber feature files to execute and zucchini runs the tests in parallel with a separate backing cucumber runtime for each device under test. Upon completion, all of these tests are compiled into a single report made available in an html format. For more details on how zucchini works as well as how to use it in your project, please see the [zucchini wiki](https://github.com/Comcast/zucchini/wiki)

![Zucchini](http://comcast.github.io/zucchini/images/zukeshield.png)


##Additional Features

 - Multiple report concatenation - When running multiple test files, regardless of tags, all tests will have their output preserved. The generated report is available by default at `target/zucchini-reports/feature-overview.html`.  Available as of version 2.0
 - Barrier sync - When the test contexts need to phased, Zucchini is able to enforce this with barrier synchronization.  The provided barrier synchronization is robust, and is able to accommodate tests that fail prior to reaching the barrier, as well as tests that get stuck or timeout.  Enabling a barrier sync is as easy as calling `Barrier.sync()`.  To enable barrier synchronization for a test, the `canBarrier` method of the `AbstractZucchiniTest` must be overridden.  Available as of version 2.2.
 - Fast run - Run through the features once using the test contexts as a thread pool.  To enable fast run for a test, the `isFastrun` method of the `AbstractZucchiniTest` must be overridden.  Available as of version 2.2.9

##Maven Integration
To pull Zucchini from maven central, add the following to the pom.xml dependencies or dependencyManagement section:

```xml
<dependency>
    <groupId>com.comcast.zucchini</groupId>
    <artifactId>zucchini</artifactId>
    <version>[2.2, 3)</version>
</dependency>
```

The provided version string is suggested, though others will work as per Maven versioning.

##More Information
For more information on what Zucchini is and how to use it please see the [zucchini wiki](https://github.com/Comcast/zucchini/wiki) 

##Submitting Issues
Please file a github issue for any problems or feature requests (or better yet, submit a pull request!)
