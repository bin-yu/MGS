import { TestBed, inject } from '@angular/core/testing';

import { DoorService } from './door.service';

describe('DoorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DoorService]
    });
  });

  it('should be created', inject([DoorService], (service: DoorService) => {
    expect(service).toBeTruthy();
  }));
});
