import { Component } from '@angular/core';
import { faSteam, faYoutube, faTwitter } from '@fortawesome/free-brands-svg-icons';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'Mattes\' Home of Streaming - stream.mattes.io';
  faSteam = faSteam
  faYoutube = faYoutube
  faTwitter = faTwitter
}
