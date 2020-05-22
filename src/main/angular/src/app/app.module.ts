import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {LiveStreamComponent} from './stream/live-stream.component';
import {YouTubeVideoComponent} from './stream/yt-video.component';
import {HttpClientModule} from '@angular/common/http';
import {CompletedStreamsComponent} from './stream/completed-streams.component';
import {SafePipe} from './util/SafePipe';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [
    AppComponent,
    SafePipe,
    LiveStreamComponent,
    YouTubeVideoComponent,
    CompletedStreamsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FontAwesomeModule
  ],
  providers: [
    HttpClientModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
