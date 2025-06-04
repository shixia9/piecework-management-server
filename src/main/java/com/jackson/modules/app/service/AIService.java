package com.jackson.modules.app.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.common.utils.R;
import com.jackson.modules.app.dto.HistoryDTO;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.vo.ImageVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface AIService extends IService<ImageEntity> {

    Long save(MultipartFile multipartFile, Long userId, String check, String picSize, Long steelId) throws IOException;


    List<ImageEntity> queryHistory(HistoryDTO historyDTO, Long userId, String check);

    ImageEntity queryById(Long id, Long userId, String check);

    byte[] getImage(String name, HttpServletResponse response, HttpServletRequest request,Long userId);

    R reSave(Long imageId, Long userId);
}
