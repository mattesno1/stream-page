import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

export interface Video {
  title: string
  date: string
  url: string
  embedUrl: string
}

export interface VideoPage {
  videos: Video[]
  nextPage: string | null
}

const PAGE_SIZE: number = 10

@Injectable({providedIn: 'root'})
export class StreamService {

  constructor(private http: HttpClient) {
  }

  getActiveLiveStream(): Observable<Video> {
    return this.http.get('/api/streams/live')
      .pipe(map(o => o as Video))
  }

  getPageOfCompletedStreams(page: String = null, pageSize: number = PAGE_SIZE): Observable<VideoPage> {
    let pageParam: string = !!page ? `&page=${page}` : ""

    return this.http.get(`/api/streams/completed?pageSize=${pageSize}${pageParam}`)
      .pipe(map(o => o as VideoPage))
  }
}
