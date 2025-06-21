package com.ea.config;

import com.ea.enums.BrowserType;
import com.ea.enums.ExecutionMode;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.Sources({
        "classpath:config.properties"
})
public interface RunConfig extends Config {

    @DefaultValue("dev")
    @Key("env")
    String env();

    @Key("${env}.recording.flag")
    boolean recordingFlag();

    @Key("${env}.browser.name")
    BrowserType browser();

    @Key("${env}.execution.mode")
    ExecutionMode executionMode();

    @Key("${env}.platform")
    String platform();

    @Key("${env}.remote.url")
    String remoteUrl();

    @Key("${env}.sap.excel.report")
    boolean sapExcelReport();

    @Key("report.path")
    String reportPath();

    @Key("evidence.folder")
    String evidenceFolder();

    @Key("implicit.timeout")
    int implicitTimeout();

    @Key("explicit.timeout")
    int explicitTimeout();

    @Key("okta.username")
    String oktaUsername();

    @Key("okta.password")
    String oktaPassword();

    @Key("sap.username")
    String sapUsername();

    @Key("sap.password")
    String sapPassword();

    @Key("sap.homepage.url")
    String sapHomePageUrl();

    @Key("okta.loginpage.url")
    String oktaLoginPageUrl();

    @Key("sap.loginpage.url")
    String sapLoginUrl();

    @Key("btp.loginpage.url")
    String btpLoginUrl();

    @Key("btp.homepage.url")
    String btpHomePageUrl();

    static RunConfig load() {
        return ConfigFactory.create(RunConfig.class);
    }
}
