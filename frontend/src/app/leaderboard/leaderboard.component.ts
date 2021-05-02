import {Component, OnInit} from '@angular/core';
import {LeaderboardEntry} from "./leaderboard-entry";
import {LeaderboardService} from "../leaderboard.service";
import {MessageService} from "../message.service";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  leaderboardEntries = []

  displayedColumns: string[] = ['rank', 'name', 'points'];

  selectedEntry?: LeaderboardEntry;
  onSelect(entry: LeaderboardEntry): void {
    this.messageService.add(`LeaderboardComponent: Selected leaderboard id=${entry.id}`);
    this.selectedEntry = entry;
  }

  constructor(private leaderboardService: LeaderboardService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.leaderboardService
        .getEntries()
        .subscribe(entries => this.leaderboardEntries = entries)
  }

}
