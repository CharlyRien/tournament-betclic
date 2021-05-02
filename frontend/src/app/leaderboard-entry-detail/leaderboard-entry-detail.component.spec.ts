import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaderboardEntryDetailComponent } from './leaderboard-entry-detail.component';

describe('LeaderboardEntryDetailComponent', () => {
  let component: LeaderboardEntryDetailComponent;
  let fixture: ComponentFixture<LeaderboardEntryDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LeaderboardEntryDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LeaderboardEntryDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
