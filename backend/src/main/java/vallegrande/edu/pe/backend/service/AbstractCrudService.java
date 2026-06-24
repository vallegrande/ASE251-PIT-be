package vallegrande.edu.pe.backend.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import vallegrande.edu.pe.backend.model.AuditableEntity;
import vallegrande.edu.pe.backend.model.CrudEntity;

@Transactional
public abstract class AbstractCrudService<T extends AuditableEntity & CrudEntity> {
	private static final String NOT_FOUND_MESSAGE_PREFIX = "Registro no encontrado: ";
	private final JpaRepository<T, Long> repository;

	protected AbstractCrudService(JpaRepository<T, Long> repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public List<T> findAll() {
		return repository.findAll().stream()
				.filter(entity -> Boolean.TRUE.equals(entity.getEstado()) && entity.getDeletedAt() == null)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<T> findAllDeleted() {
		return repository.findAll().stream()
				.filter(entity -> !Boolean.TRUE.equals(entity.getEstado()) || entity.getDeletedAt() != null)
				.toList();
	}

	@Transactional(readOnly = true)
	public T findById(Long id) {
		return repository.findById(id)
				.filter(entity -> Boolean.TRUE.equals(entity.getEstado()) && entity.getDeletedAt() == null)
				.orElseThrow(() -> new NoSuchElementException(NOT_FOUND_MESSAGE_PREFIX + id));
	}

	public T create(T entity) {
		entity.setId(null);
		entity.setEstado(Boolean.TRUE);
		entity.setDeletedAt(null);
		entity.setRestoredAt(null);
		return repository.save(entity);
	}

	public T update(Long id, T incoming) {
		T existing = repository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(NOT_FOUND_MESSAGE_PREFIX + id));
		BeanUtils.copyProperties(incoming, existing, "id", "estado", "createdAt", "updatedAt", "deletedAt", "restoredAt");
		return repository.save(existing);
	}

	public void delete(Long id) {
		T existing = repository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(NOT_FOUND_MESSAGE_PREFIX + id));
		existing.markDeleted();
		repository.save(existing);
	}

	public T restore(Long id) {
		T existing = repository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(NOT_FOUND_MESSAGE_PREFIX + id));
		existing.markRestored();
		return repository.save(existing);
	}
}
