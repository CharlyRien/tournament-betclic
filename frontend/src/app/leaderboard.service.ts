import {Injectable} from '@angular/core';
import {LeaderboardEntry} from "./leaderboard/leaderboard-entry";
import {Observable} from "rxjs";
import {MessageService} from './message.service';
import {HttpClient} from '@angular/common/http';
import {tap} from "rxjs/operators";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  apiPort = environment['API_REST_PORT'];
  apiUrl = environment['API_REST_URL'];

  private leaderboardURL = 'players';

  getEntries(): Observable<Array<LeaderboardEntry>> {
    return this.http.get<Array<LeaderboardEntry>>(`${this.apiUrl}:${this.apiPort}/players`).pipe(
        tap(_ => this.log("leaderboard entries fetched."))
    );
  }

  private log(message: string) {
    this.messageService.add(`LeaderboardService: ${message}`);
  }


  constructor(private http: HttpClient,
              private messageService: MessageService) { }
}
