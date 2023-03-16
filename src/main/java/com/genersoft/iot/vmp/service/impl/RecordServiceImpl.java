package com.genersoft.iot.vmp.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.genersoft.iot.vmp.service.IRecordService;
import com.genersoft.iot.vmp.storager.dao.RecordMapper;
import com.genersoft.iot.vmp.vmanager.bean.Record;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * 空间数据集接口
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements IRecordService {

    @Async
    public Boolean saveOrUpdate(@RequestParam String deviceId,
                                @RequestParam String recordId,
                                @RequestParam String startTime,
                                String appId,
                                String endTime, String type) {


        if ("add".equals(type)) {
            Record record = new Record();
            record.setCreateTime(new Date());
            record.setDeviceId(deviceId);
            record.setRecordId(recordId);
            record.setAppId(appId);
            this.save(record);
        } else {

            List<Record> list = new LambdaQueryChainWrapper<Record>(baseMapper)
                    .eq(Record::getDeviceId, deviceId)
                    .eq(Record::getAppId, appId).orderByDesc(Record::getCreateTime).last(" limit 1").list();
            Record record = list.get(0);

            boolean update = new LambdaUpdateChainWrapper<Record>(this.getBaseMapper())
                    .eq(Record::getId, record.getId())
                    .set(null != startTime, Record::getStartTime, startTime)
                    .set(null != endTime, Record::getEndTime, endTime)
                    .update();
        }
        return true;
    }
}
