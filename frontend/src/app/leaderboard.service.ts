import {Injectable} from '@angular/core';
import {LeaderboardEntry} from "./leaderboard/leaderboard-entry";
import {Observable, Subject, timer} from "rxjs";
import {HttpClient} from '@angular/common/http';
import {environment} from "../environments/environment";
import {retry, share, switchMap, takeUntil} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  apiPort = environment['API_REST_PORT'];
  apiUrl = environment['API_REST_URL'];

  getEntries(): Observable<Array<LeaderboardEntry>> {
    return timer(1, 3000).pipe(
        switchMap(() => this.http.get<Array<LeaderboardEntry>>(`${this.apiUrl}:${this.apiPort}/api/players`)),
        retry(),
        share(),
        takeUntil(this.stopPolling)
    )
  }

  private stopPolling = new Subject();

  ngOnDestroy() {
    this.stopPolling.next();
  }

  constructor(private http: HttpClient) { }
}
