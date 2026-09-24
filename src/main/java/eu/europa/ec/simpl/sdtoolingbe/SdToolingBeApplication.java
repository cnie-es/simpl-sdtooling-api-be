package eu.europa.ec.simpl.sdtoolingbe;

import eu.europa.ec.simpl.data1.common.util.ApplicationUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class SdToolingBeApplication {

    public static void main(String[] args) {
        ApplicationUtil.printBannerAndRun(
                new SpringApplication(SdToolingBeApplication.class), args, "banner.txt", SdToolingBeApplication.class);
    }
}
