package com.bawnorton.trimica.api;

import com.bawnorton.trimica.api.impl.TrimicaApiImpl;

public interface TrimicaApi {
    static TrimicaApi getInstance() {
        return TrimicaApiImpl.INSTANCE;
    }

    void registerBaseTextureInterceptor(BaseTextureInterceptor baseTextureInterceptor);
}
