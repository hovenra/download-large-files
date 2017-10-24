import {Component, NgModule} from '@angular/core';
import { DownloadService } from './download.service';

@NgModule({
  // Other properties removed
  providers: [DownloadService],

})

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
}
