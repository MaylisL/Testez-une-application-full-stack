import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { TeacherService } from 'src/app/services/teacher.service';
import { Session } from '../../interfaces/session.interface';
import { SessionApiService } from '../../services/session-api.service';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';


const fakeSession: Session = {
  id: 1,
  name: 'Yoga basics',
  description: 'yoga Session',
  date: '2025-10-10' as unknown as Date,
  teacher_id: 5,
  users: [1, 2, 3]
};


const fakeTeacher: Partial<Teacher> = {
  id: 5,
  lastName: 'YogaTeacherLastName',
  firstName: 'YogaTeacherFirstName'
};

const mockActivatedRoute = {
  snapshot: {
    paramMap: convertToParamMap({ id: '1' })
  }
};

//Test unitaires
describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let service: SessionService;

  let mockSessionService: Partial<SessionService>;
  let mockSessionApiService: Partial<SessionApiService>;
  let mockTeacherService: Partial<TeacherService>;
  let mockMatSnackBar: Partial<MatSnackBar>;
  let mockRouter: Partial<Router>;
  let formBuilder: FormBuilder;
  let spyWindowHistoryBack: jest.SpyInstance;


  beforeEach(async () => {

    spyWindowHistoryBack = jest.spyOn(window.history, 'back').mockImplementation(() => { });

    mockMatSnackBar = {
      open: jest.fn()
    };
    mockSessionService = {
      sessionInformation: {
        id: 1,
        admin: true,
        token: 'fake-token',
        type: 'USER',
        username: 'testUserName',
        firstName: 'TestFirstName',
        lastName: 'TestLastName'
      }
    };

    mockRouter = {
      navigate: jest.fn()
    };

    formBuilder = new FormBuilder();

    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(fakeSession)),
      delete: jest.fn().mockReturnValue(of(void 0)),
      participate: jest.fn().mockReturnValue(of(void 0)),
      unParticipate: jest.fn().mockReturnValue(of(void 0))
    };
    mockTeacherService = {
      detail: jest.fn().mockReturnValue(of(fakeTeacher))
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatSnackBarModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: FormBuilder, useValue: formBuilder },
      ],
    })
      .compileComponents();
    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    spyWindowHistoryBack.mockRestore();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('should call sessionApiService.detail and teacherService.detail on ngOnInit', () => {
    component.ngOnInit();

    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(mockTeacherService.detail).toHaveBeenCalledWith(fakeSession.teacher_id.toString());
    expect(component.session).toEqual(fakeSession);
    expect(component.teacher).toEqual(fakeTeacher);
  });

  it('should call window.history.back() when back() is called', () => {
    component.back();

    expect(spyWindowHistoryBack).toHaveBeenCalled();
    spyWindowHistoryBack.mockRestore();
  });


  it('should delete the account and redirect', () => {
    component.delete();

    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Session deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should call sessionApiService.participate on participate', () => {
    component.participate();

    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1');
  });


  it('should call sessionApiService.unParticipate on unParticipate', () => {
    component.unParticipate();

    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
  });

});

//tests d'intégration
describe('DetailComponent - Integration Test (fetchSession)', () => {
  let fixture: ComponentFixture<DetailComponent>;
  let component: DetailComponent;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        SessionService,
        SessionApiService,
        TeacherService,
        FormBuilder,
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } } }
      ]
    }).compileComponents();


    httpMock = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);

    sessionService.sessionInformation = {
      id: 1,
      admin: true,
      token: 'fake-token',
      type: 'USER',
      username: 'testUserName',
      firstName: 'TestFirstName',
      lastName: 'TestLastName'
    };
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should fetch session and teacher, then update component state', () => {

    const reqSession = httpMock.expectOne('api/session/1');
    expect(reqSession.request.method).toBe('GET');
    reqSession.flush(fakeSession);

    const reqTeacher = httpMock.expectOne('api/teacher/5');
    expect(reqTeacher.request.method).toBe('GET');
    reqTeacher.flush(fakeTeacher);

    expect(component.session).toEqual(fakeSession);
    expect(component.teacher).toEqual(fakeTeacher);
    expect(component.isParticipate).toBe(true);
  });

});

