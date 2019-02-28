package site.cnkj.prometheus.service;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;

/*
 * @version 1.0 created by LXW on 2019/2/26 15:32
 */
@Aspect
@Component
@Slf4j
public class PrometheusMetricsAspect {
    private static final Counter requestTotal = Counter.build()
            .name("counter_all")
            .labelNames("api")
            .help("total request counter of api")
            .register();
    private static final Counter requestError = Counter.build()
            .name("counter_error")
            .labelNames("api")
            .help("response Error counter of api")
            .register();
    private static final Histogram histogram = Histogram.build()
            .name("histogram_consuming")
            .labelNames("api")
            .help("response consuming of api")
            .register();

    // 自定义Prometheus注解的全路径
    @Pointcut("@annotation(site.cnkj.prometheus.service.PrometheusMetrics)")
    public void pcMethod() {

    }

    @Around(value="pcMethod() && @annotation(annotation)")
    public Object MetricsCollector(ProceedingJoinPoint joinPoint, PrometheusMetrics annotation) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        PrometheusMetrics prometheusMetrics = methodSignature.getMethod().getAnnotation(PrometheusMetrics.class);
        if (prometheusMetrics != null) {
            String name;
            if (StringUtils.isNotEmpty(prometheusMetrics.name()) ){
                name = prometheusMetrics.name();
            }else{
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                name = request.getRequestURI();
            }
            requestTotal.labels(name).inc();
            Histogram.Timer requestTimer = histogram.labels(name).startTimer();
            Object object;
            try {
                object = joinPoint.proceed();
            } catch (Exception e) {
                requestError.labels(name).inc();
                throw e;
            } finally {
                requestTimer.observeDuration();
            }
            return object;
        } else {
            return joinPoint.proceed();
        }
    }
}
