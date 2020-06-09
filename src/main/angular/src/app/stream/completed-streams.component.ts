import {Component, Input, OnInit} from '@angular/core';
import {StreamService, Video} from './stream.service';
import {DateService} from './date.service';

@Component({
  selector: 'completed-streams',
  template: `
    <div class="row">
      <div *ngFor="let video of completedStreams" class="col-lg-4 col-sm-6 col-xs-12 mb-4">
        <div class="card h-100">
          <yt-video [url]="video.embedUrl"></yt-video>
          <div class="card-body">
            <p class="card-title">
              <a href="{{ video.url }}">{{ video.title }}</a>
            </p>
          </div>
          <small class="card-footer text-muted">{{ formatDate(video.date) }}</small>
        </div>
      </div>
    </div>

    <div class="page-link text-center mb-3" *ngIf="hasMore()">
      <div *ngIf="isFetching()" class="spinner-border" role="status">
        <span class="sr-only">loading...</span>
      </div>
      <button *ngIf="!isFetching()" class="btn btn-link p-0" (click)="getMore()">load more</button>
    </div>
  `
})

export class CompletedStreamsComponent implements OnInit {

  @Input()
  public pageSize: number
  public completedStreams: Video[] = []

  private nextPage: string = null
  private fetching: boolean = false

  constructor(private streamService: StreamService,
              private dateService: DateService) {
  }

  ngOnInit(): void {
    this.fetchPageOfVideos()
  }

  public hasMore(): boolean {
    return !!this.nextPage
  }

  public isFetching(): boolean {
    return this.fetching
  }

  public getMore() {
    if (!this.fetching) {
      this.fetching = true
      this.fetchPageOfVideos()
    }
  }

  formatDate(isoString: string): string {
    let timestamp = this.dateService.parseIso(isoString);
    return `${timestamp.format('LLLL')}`
  }

  private fetchPageOfVideos() {
    this.streamService.getPageOfCompletedStreams(this.nextPage, this.pageSize).subscribe(
      page => {
        for (let video of page.videos) {
          this.completedStreams.push(video)
        }
        this.nextPage = page.nextPage
        this.fetching = false
      },
      () => {
        console.error("could not retrieve streams")
        this.fetching = false
      }
    )
  }
}
