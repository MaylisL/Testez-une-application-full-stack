import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, flush, TestBed, tick } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { Session } from 'src/app/features/sessions/interfaces/session.interface';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { FormComponent } from './form.component';

const fakeSession: Session = {
  id: 1,
  name: 'Yoga Basics',
  description: 'yoga for beginners',
  date: '2025-10-12' as unknown as Date,
  teacher_id: 10,
  users: [1, 2, 3]
};

const mockActivatedRoute = {
  snapshot: {
    paramMap: {
      get: jest.fn().mockReturnValue('1')
    }
  }
} as unknown as ActivatedRoute;

const mockSessionService = {
  sessionInformation: {
    id: 1,
    admin: true,
    token: 'fake-token',
    type: 'USER',
    username: 'testuser',
    firstName: 'Testfirstname',
    lastName: 'Testlastname'
  }
};

const mockMatSnackBar = {
  open: jest.fn()
} as unknown as MatSnackBar;

const mockSessionApiService = {
  detail: jest.fn().mockReturnValue(of(fakeSession)),
  create: jest.fn().mockReturnValue(of(fakeSession)),
  update: jest.fn().mockReturnValue(of(fakeSession))
} as unknown as SessionApiService;

const mockTeacherService = {
  all: jest.fn().mockReturnValue(of([{ id: 1, firstName: 'Testfirstname', lastName: 'Testlastname' }]))
} as unknown as TeacherService;

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockRouter = {
  navigate: jest.fn(),
  url: '/sessions/update'
} as unknown as Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        FormBuilder,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: Router, useValue: mockRouter }
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

it('should have an invalid form when required fields are empty', () => {
  expect(component.sessionForm).toBeDefined();

  component.sessionForm!.setValue({
    name: '',
    date: '',
    teacher_id: '',
    description: ''
  });
  expect(component.sessionForm!.valid).toBeFalsy();
  expect(component.sessionForm!.get('name')!.hasError('required')).toBeTruthy();
  expect(component.sessionForm!.get('date')!.hasError('required')).toBeTruthy();
  expect(component.sessionForm!.get('teacher_id')!.hasError('required')).toBeTruthy();
  expect(component.sessionForm!.get('description')!.hasError('required')).toBeTruthy();
});

it('should have a valid form when all required fields are filled', () => {
  expect(component.sessionForm).toBeDefined();

  component.sessionForm!.setValue({
    name: 'Yoga Basics',
    date: '2025-10-12',
    teacher_id: 10,
    description: 'yoga for beginners'
  });

  expect(component.sessionForm!.valid).toBeTruthy();
});

  it('should redirect non-admin users to /sessions', () => {
    mockSessionService.sessionInformation.admin = false;

    component.ngOnInit();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should not navigate to /sessions if admin', () => {
    jest.clearAllMocks();
    mockSessionService.sessionInformation.admin = true;

    component.ngOnInit();

    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should get the details and init form if update is in url', () => {
    component.ngOnInit();

    expect(component.onUpdate).toEqual(true);
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
  });
});

// tests d'intégration
describe('FormComponent - Integration Tests', () => {
  let fixture: ComponentFixture<FormComponent>;
  let component: FormComponent;
  let httpMock: HttpTestingController;
  let matSnackBar: MatSnackBar;
  let router: Router;
  let sessionApiService: SessionApiService;
  let sessionService: SessionService;

 beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]),
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        NoopAnimationsModule,
      ],
      declarations: [FormComponent],
      providers: [
        FormBuilder,
        SessionService,
        SessionApiService,
        TeacherService,
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } } }
      ]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    matSnackBar = TestBed.inject(MatSnackBar);

    router = TestBed.inject(Router);
    sessionApiService = TestBed.inject(SessionApiService);
    sessionService = TestBed.inject(SessionService);

    sessionService.sessionInformation = {
      id: 1,
      admin: true,
      token: 'fake-token',
      type: 'USER',
      username: 'admin',
      firstName: 'Testfirstname',
      lastName: 'Testlastname'
    };

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

  });

  it('should call create(), show snackbar, and navigate on submit (create mode)', fakeAsync(()=> {
    component.onUpdate = false;
    component.sessionForm?.setValue({
      name: 'Session1',
      date: '2025-10-12',
      teacher_id: 10,
      description: 'Description'
    });

    const snackSpy = jest.spyOn(matSnackBar, 'open');
    const routerSpy = jest.spyOn((component as any).router, 'navigate')
    .mockImplementation(() => Promise.resolve(true));
    component.submit();

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.name).toBe('Session1');

    req.flush(fakeSession);

    tick()
    flush();
    expect(snackSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  }));
});
