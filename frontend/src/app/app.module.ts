import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppComponent} from './app.component';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LeaderboardComponent} from './leaderboard/leaderboard.component';
import {FormsModule} from "@angular/forms";
import {MatTableModule} from '@angular/material/table';
import {LeaderboardEntryDetailComponent} from './leaderboard-entry-detail/leaderboard-entry-detail.component';
import {HttpClientModule} from '@angular/common/http';

@NgModule({
    declarations: [
        AppComponent,
        LeaderboardComponent,
        LeaderboardEntryDetailComponent
    ],
    imports: [
        HttpClientModule,
        MatTableModule,
        FormsModule,
        BrowserModule,
        BrowserAnimationsModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
