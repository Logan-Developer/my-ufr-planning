package fr.loganh.myufrplanning.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.loganh.myufrplanning.api.SednaService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideSednaService(): SednaService {
        return SednaService.create()
    }
}