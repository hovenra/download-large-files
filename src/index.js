import {Component, View} from 'angular2/core';
import {bootstrap} from 'angular2/platform/browser';
import {DownloadLargeFiles} from 'download-large-files';

@Component({
  selector: 'main'
})

@View({
  directives: [DownloadLargeFiles],
  template: `
    <download-large-files></download-large-files>
  `
})

class Main {

}

bootstrap(Main);
