import {Component, Input} from '@angular/core';

@Component({
  selector: 'yt-video',
  template: `
    <div class="embed-responsive embed-responsive-16by9">
      <iframe [src]="url + '?autoplay=' + (autoplay ? 1 : 0) | safe" class="embed-responsive-item"
              allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen>
      </iframe>
    </div>
  `
})

export class YouTubeVideoComponent {

  @Input()
  public url: string

  @Input()
  public autoplay: boolean = false
}
