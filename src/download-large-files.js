import {Component, View} from 'angular2/core';

@Component({
  selector: 'download-large-files'
})

@View({
  templateUrl: 'download-large-files.html'
})

export class DownloadLargeFiles {

  constructor() {
    console.info('DownloadLargeFiles Component Mounted Successfully');
  }

}
