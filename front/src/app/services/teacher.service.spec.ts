import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Teacher } from '../interfaces/teacher.interface';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const fakeTeacher: Partial<Teacher> = {
    id: 1,
    firstName: 'TestFirstName',
    lastName: 'TestLastName'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ],
      providers: [TeacherService]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });
  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all teachers', () => {
    service.all().subscribe((teachers) => {
      expect(teachers).toEqual([fakeTeacher]);
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush([fakeTeacher]);
  });

  it('should fetch teacher details', () => {
    service.detail('1').subscribe((teacher) => {
      expect(teacher).toEqual(fakeTeacher);
    });

    const req = httpMock.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(fakeTeacher);
  });
});
