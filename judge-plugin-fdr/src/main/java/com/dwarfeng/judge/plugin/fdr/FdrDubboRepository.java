package com.dwarfeng.judge.plugin.fdr;

import com.dwarfeng.dcti.stack.bean.dto.TimedValue;
import com.dwarfeng.fdr.stack.service.MappingLookupService;
import com.dwarfeng.judge.impl.handler.Repository;
import com.dwarfeng.judge.stack.exception.RepositoryException;
import com.dwarfeng.subgrade.stack.bean.key.LongIdKey;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 符合Judge框架仓库接口的Fdr仓库，使用Dubbo协议。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
@Component
public class FdrDubboRepository implements Repository {

    public static final String SUPPORT_TYPE = "fdr.dubbo";

    @Autowired
    @Qualifier("fdrDubboRepository.mappingLookupService")
    private MappingLookupService mappingLookupService;

    @Override
    public boolean supportType(String type) {
        return Objects.equals(SUPPORT_TYPE, type);
    }

    @Override
    public TimedValue realtimeValue(LongIdKey pointKey) throws RepositoryException {
        try {
            return mappingLookupService.lookupRealtime(pointKey);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<TimedValue> realtimeValue(LongIdKey pointKey, String processPreset, Object[] args)
            throws RepositoryException {
        try {
            return mappingLookupService.mappingRealtime(pointKey, processPreset, args);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<TimedValue> persistenceValue(LongIdKey pointKey, Date startDate, Date endDate)
            throws RepositoryException {
        try {
            return mappingLookupService.lookupPersistence(pointKey, startDate, endDate);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<TimedValue> persistenceValue(
            LongIdKey pointKey, Date startDate, Date endDate, String processPreset, Object[] args)
            throws RepositoryException {
        try {
            return mappingLookupService.mappingPersistence(pointKey, startDate, endDate, processPreset, args);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Configuration
    public static class FdrDubboRepositoryConfiguration {

        @Value("${repository.fdr.dubbo.timeout}")
        private int timeout;
        @Value("${repository.fdr.dubbo.retries}")
        private int retries;

        @Bean("fdrDubboRepository.mappingLookupService")
        public ReferenceBean<MappingLookupService> mappingLookupServiceReferenceBean() {
            ReferenceBean<MappingLookupService> reference = new ReferenceBean<>();
            reference.setId("fdrDubboRepository.mappingLookupService");
            reference.setTimeout(timeout);
            reference.setRetries(retries);
            reference.setInterface(MappingLookupService.class);
            return reference;
        }
    }
}
