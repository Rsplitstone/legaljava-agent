import {
  useQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/react-query';
import {
  DragDropContext,
  Droppable,
  Draggable,
  DropResult,
} from '@hello-pangea/dnd';
import { fetchColumns, updateColumns } from '../services/api';

interface Task {
  id: string;
  name: string;
}

interface Column {
  id: string;
  title: string;
  tasks: Task[];
}

export default function Schedule() {
  const qc = useQueryClient();

  /** 1 Fetch columns (defaults to [] on first render) */
  const {
    data: columns = [],
    isLoading,
    isError,
  } = useQuery<Column[]>({
    queryKey: ['columns'],
    queryFn: fetchColumns,
    initialData: [],
  });

  /** 2 Persist drag-and-drop mutations with optimistic UI */
  const mutateCols = useMutation({
    mutationFn: updateColumns,
    onMutate: async (next: Column[]) => {
      await qc.cancelQueries({ queryKey: ['columns'] });
      const prev = qc.getQueryData<Column[]>(['columns']) ?? [];
      qc.setQueryData(['columns'], next);
      return { prev };
    },
    onError: (_e, _v, ctx) => {
      if (ctx?.prev) qc.setQueryData(['columns'], ctx.prev);
    },
    onSettled: () => qc.invalidateQueries({ queryKey: ['columns'] }),
  });

  /** 3 Handle drag-end */
  function onDragEnd(result: DropResult) {
    if (!result.destination) return;

    const srcColIdx = columns.findIndex(c => c.id === result.source.droppableId);
    const dstColIdx = columns.findIndex(c => c.id === result.destination!.droppableId);
    if (srcColIdx < 0 || dstColIdx < 0) return;

    const srcCol = { ...columns[srcColIdx] };
    const dstCol = srcColIdx === dstColIdx ? srcCol : { ...columns[dstColIdx] };

    const [moved] = srcCol.tasks.splice(result.source.index, 1);
    dstCol.tasks.splice(result.destination.index, 0, moved);

    const next = [...columns];
    next[srcColIdx] = srcCol;
    next[dstColIdx] = dstCol;

    mutateCols.mutate(next);
  }

  /* ---------- render ---------- */
  if (isLoading) return <p className="p-4">Loading board…</p>;
  if (isError)   return <p className="p-4 text-red-600">Cannot load board.</p>;
  if (!columns.length)
    return <p className="p-4">No columns available. Add tasks to get started.</p>;

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <div className="flex gap-6 overflow-x-auto p-4">
        {columns.map(col => (
          <Droppable droppableId={col.id} key={col.id}>
            {prov => (
              <section
                ref={prov.innerRef}
                {...prov.droppableProps}
                className="w-72 shrink-0 rounded-xl bg-gray-100 dark:bg-gray-800 p-3 space-y-3"
              >
                <h3 className="font-semibold">{col.title}</h3>
                {col.tasks.map((t, i) => (
                  <Draggable draggableId={t.id} index={i} key={t.id}>
                    {p => (
                      <article
                        ref={p.innerRef}
                        {...p.draggableProps}
                        {...p.dragHandleProps}
                        className="rounded-lg bg-white dark:bg-gray-700 p-3 border-l-4 border-accent"
                      >
                        {t.name}
                      </article>
                    )}
                  </Draggable>
                ))}
                {prov.placeholder}
              </section>
            )}
          </Droppable>
        ))}
      </div>
    </DragDropContext>
  );
}
