Zucchini
========
[![Build Status](https://travis-ci.org/Comcast/zucchini.svg)](https://travis-ci.org/Comcast/zucchini)

![ZOMBIE ZUKES](http://www.fruitycuties.com/images/humour/138-cartoon-zucchini-joke.gif)
Image courtesy of [http://www.fruitycuties.com/](http://www.fruitycuties.com/)

##Summary

Zucchini is a layer that sits on top of cucumber-jvm and provides higher testing throughput with additional utilities.  Zucchini takes advantage of situations where a set if tests needs to be run with multiple configurations.  In such cases, each configuration is run in parallel with a separate backing cucumber runtime.  Upon completion, all of these tests are compiled into a single report made available in an html format.

##Additional Features

 - Multiple report concatenation - When running multiple test files, regardless of tags, all tests will have their output preserved. The generated report is available by default at `target/zucchini-reports/feature-overview.html`.  Available as of version 2.0
 - Barrier sync - When the test contexts need to phased, Zucchini is able to enforce this with barrier synchronization.  The provided barrier synchronization is robust, and is able to accommodate tests that fail prior to reaching the barrier, as well as tests that get stuck or timeout.  Enabling a barrier sync is as easy as calling `Barrier.sync()`.  Available as of version 2.2.

##More Information
For more information on what Zucchini is and how to use it please see :  [http://comcast.github.io/zucchini/](http://comcast.github.io/zucchini/)
