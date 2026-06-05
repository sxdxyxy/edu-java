package com.joyfishs.dawa.practice.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.practice.entity.PracticeCollection;
import com.joyfishs.dawa.practice.mapper.XmPracticeCollectionMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class XmPracticeCollectionService extends ServiceImpl<XmPracticeCollectionMapper, PracticeCollection> {
}
