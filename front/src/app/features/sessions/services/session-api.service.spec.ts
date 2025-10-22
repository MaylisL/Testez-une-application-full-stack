import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const fakeSession: Session = {
    id: 1,
    name: 'Yoga Basics',
    description: 'Beginners course',
    date: new Date('2025-10-12'),
    teacher_id: 10,
    users: [1, 2, 3]
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule,
        HttpClientTestingModule,
      ],providers: [
        SessionApiService
      ]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all sessions', () => {
    service.all().subscribe((sessions) => {
      expect(sessions).toEqual([fakeSession]);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');

    req.flush([fakeSession]);
  });

  it('should fetch session details', () => {
    service.detail('1').subscribe((session) => {
      expect(session).toEqual(fakeSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');

    req.flush(fakeSession);
  });

  it('should create a new session', () => {
    service.create(fakeSession).subscribe((session) => {
      expect(session).toEqual(fakeSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    req.flush(fakeSession);
  });

  it('should update a session', () => {
    service.update('1', fakeSession).subscribe((session) => {
      expect(session).toEqual(fakeSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    req.flush(fakeSession);
  });

  it('should delete a session', () => {
    service.delete('1').subscribe((res) => {
      expect(res).toBeTruthy();
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
