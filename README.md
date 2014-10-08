Zucchini
========
![ZOMBIE ZUKES](http://www.fruitycuties.com/images/humour/138-cartoon-zucchini-joke.gif)

What is Zucchini?
-----------------
Zucchini allows you the flexibility to run a cucumber test on multiple different targets either in parallel or serially depending on your needs.

How do I use it?
----------------
* Write your cucumber feature files as you normally would
* Implement the `AbstractZucchiniTest` in your TestNG test case.
* In your AbstractZucchiniTest, override this method `public List<TestContext> getTestContexts()`
* In the getTestContexts method, create testContexts for each of the devices you want to run against.  (note that a device can be anything you can run your cucumber test against - not just physical devices, like phones, but also browsers, servers, etc)
* In your glue code, get access to the data in the TestContext by calling this method:	`TestContext.getCurrent()`
* Use the device in your glue code for fun and profit!

How do I add it to my pom?
--------------------------
```xml
<dependency>
  <groupId>com.comcast.zucchini</groupId>
  <artifactId>zucchini</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

