import {Component} from '@angular/core';
import {StreamService} from './stream.service';

@Component({
  selector: 'live-stream',
  template: `
    <div class="card h-100">
      <yt-video *ngIf="isLive()" [url]="liveStreamUrl" autoplay="true"></yt-video>
      <div *ngIf="!isLive()" class="card-body">
        <h4 class="card-title">
          Currently Offline :(
        </h4>
        Check out my recent streams below for some Sw1ft St4bbIng 
      </div>
    </div>
  `
})

export class LiveStreamComponent {

  public liveStreamUrl: String

  constructor(private streamService: StreamService) {
    this.fetchCurrentLiveStream()
  }

  public isLive(): boolean {
    return !!this.liveStreamUrl
  }

  private fetchCurrentLiveStream() {
    this.streamService.getActiveLiveStream().subscribe(
      video => this.liveStreamUrl = video?.embedUrl
    )
  }
}
