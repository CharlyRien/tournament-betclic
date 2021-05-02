import {Component, Input, OnInit} from '@angular/core';
import {LeaderboardEntry} from "../leaderboard/leaderboard-entry";

@Component({
  selector: 'app-leaderboard-entry-detail',
  templateUrl: './leaderboard-entry-detail.component.html',
  styleUrls: ['./leaderboard-entry-detail.component.css']
})
export class LeaderboardEntryDetailComponent implements OnInit {

  @Input() entry?: LeaderboardEntry;

  constructor() { }

  ngOnInit(): void {
  }

}
