package com.trungquan.nongsan.config;

import com.paypal.base.rest.APIContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaypalConfiguration {

    String clientId = "Ad8mdfcIxc1GKAY0vo4urYl7joJRg9BA4TYbcwEzNRcAQBSr6dxXSENiksEU1jJm0Au4J4yHAAQ8A5bj";

    String clientSecret = "EHU5_9MUs6iiH35higp6ih14tP__ZvbWGxMeKhOwwsOC9hEvEfuVIKUFeNN4Ak7X1MWKW20TkiAymnXH";

    String mode = "sandbox";

    @Bean
    public APIContext apiContext(){
        return new APIContext(clientId, clientSecret, mode);
    }

}
